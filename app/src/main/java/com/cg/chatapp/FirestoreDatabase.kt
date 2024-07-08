package com.cg.chatapp

import com.cg.chatapp.model.*
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreDatabase {
    private val db = Firebase.firestore

    suspend fun getEmployees(): List<Employee> {
        var list: List<Employee> = mutableListOf()

        db.collection(EMPLOYEES_TABLE)
            .get()
            .addOnSuccessListener { result ->
                list = result.documents.map {
                    it.toObject(Employee::class.java) ?: Employee()
                }
            }.await()

        return list
    }

    suspend fun getEditEmployeeByUsername(username: String): EditEmployee {
        var employee = Employee()

        db.collection(EMPLOYEES_TABLE)
            .document(username)
            .get()
            .addOnSuccessListener {
                employee = it.toObject(Employee::class.java) ?: Employee()
            }.await()

        return employee.toEditEmployee()
    }

    suspend fun getEmployeeByUsername(username: String): Employee {
        var employee = Employee()

        db.collection(EMPLOYEES_TABLE)
            .document(username)
            .get()
            .addOnSuccessListener {
                employee = it.toObject(Employee::class.java) ?: Employee()
            }.await()

        return employee
    }

    suspend fun getRoles() =
        getEmployees().map {
            it.role
        }.toSet()

    suspend fun addUser(username: String, user: EditUser) {
        db.collection(USERS_TABLE)
            .document(username)
            .set(user.toUser())
            .await()
    }

    suspend fun addEmployee(username: String, employee: EditEmployee) {
        db.collection(EMPLOYEES_TABLE)
            .document(username)
            .set(employee.toEmployee())
            .await()
    }

    suspend fun updateEmployee(username: String, employee: EditEmployee) {
        db.collection(EMPLOYEES_TABLE)
            .document(username)
            .get()
            .addOnSuccessListener {
                updateDocument(it.id, employee)
            }.await()
    }

    suspend fun deleteEmployeeByUsername(username: String) {
        db.collection(EMPLOYEES_TABLE)
            .document(username)
            .get()
            .addOnSuccessListener {
                deleteDocument(it.id)
            }.await()
    }

    private fun deleteDocument(id: String) {
        db.collection(EMPLOYEES_TABLE).document(id).delete()
    }

    private fun updateDocument(id: String, employee: EditEmployee) {
        db.collection(EMPLOYEES_TABLE).document(id)
            .update(
                mapOf(
                    NAME to employee.name.value,
                    START_DATE to employee.startDate.value,
                    SKILL to employee.skill.value,
                    ROLE to employee.role.value,
                    NORM to employee.norm.value
                )
            )
    }

    suspend fun getUser(username: String): User {
        var user = User()

        db.collection(USERS_TABLE)
            .document(username)
            .get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java) ?: User()
            }.await()

        return user
    }

    private suspend fun getUsernames(): List<String> {
        var users: List<String> = emptyList()

        db.collection(USERS_TABLE)
            .get()
            .addOnSuccessListener { result ->
                users = result.documents.map {
                    it.toObject(User::class.java)?.username ?: ""
                }
            }.await()

        return users
    }

    suspend fun addMessage(sender: String, receiver: String, message: Message) {
        val encryptedMessage = Message(
            message = message.message.encrypt(getEncryptionKey(sender, receiver)),
            time = message.time
        )

        db.collection(EMPLOYEES_TABLE)
            .document(sender)
            .collection(receiver)
            .add(encryptedMessage)
            .await()
    }

    private suspend fun getReceiversForUser(sender: String): List<String> {
        val receivers = mutableListOf<String>()

        getUsernames().forEach { username ->
            db.collection(EMPLOYEES_TABLE)
                .document(sender)
                .collection(username)
                .get()
                .addOnSuccessListener {
                    if (it.size() > 0) receivers.add(username)
                }.await()
        }

        return receivers
    }

    suspend fun getSentMessagesList(sender: String, receiver: String): List<Message> {
        var list: List<Message> = mutableListOf()

        db.collection(EMPLOYEES_TABLE)
            .document(sender)
            .collection(receiver)
            .get()
            .addOnSuccessListener { result ->
                list = result.documents.map {
                    it.toObject(Message::class.java) ?: Message()
                }
            }.await()

        val x = list.map {
            Message(
                it.message.decrypt(getEncryptionKey(sender, receiver)),
                it.time
            )
        }
        return x
    }

    private suspend fun getLastMessageWithUser(sender: String, receiver: String): ChatsItem {
        var lastSenderMessage = Message()
        var lastReceiverMessage = Message()

        db.collection(EMPLOYEES_TABLE)
            .document(sender)
            .collection(receiver)
            .orderBy(TIME, Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                lastSenderMessage = result.documents.map {
                    it.toObject(Message::class.java)
                }.firstOrNull() ?: Message(time = Date(1, 2, 3))
            }.await()

        db.collection(EMPLOYEES_TABLE)
            .document(receiver)
            .collection(sender)
            .orderBy(TIME, Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                lastReceiverMessage = result.documents.map {
                    it.toObject(Message::class.java)
                }.firstOrNull() ?: Message(time = Date(1, 2, 3))
            }.await()

        val employee = getEditEmployeeByUsername(receiver).toEmployee()

        return when {
            lastSenderMessage.time >= lastReceiverMessage.time -> {
                return ChatsItem(receiver, employee.name, lastSenderMessage)
            }
            lastReceiverMessage.time > lastSenderMessage.time -> {
                return ChatsItem(receiver, employee.name, lastReceiverMessage)
            }
            else -> ChatsItem()
        }
    }

    suspend fun getChatsList(sender: String): List<ChatsItem> {
        val receivers = getReceiversForUser(sender)

        val chatsItems = mutableListOf<ChatsItem>()

        receivers.forEach { receiver ->
            val message = getLastMessageWithUser(sender, receiver)
            if (message.username.isNotEmpty()) {
                chatsItems.add(message)
            }
        }

        val returnChatsItem = chatsItems.sortedByDescending {
            it.message.time
        }.map {
            ChatsItem(
                it.username,
                it.employeeName,
                Message(
                    it.message.message.decrypt(getEncryptionKey(sender, it.username)),
                    it.message.time
                )
            )
        }

        return returnChatsItem
    }

    fun messageListener(
        coroutineScope: CoroutineScope,
        sender: String,
        receiver: String,
        onUpdate: ((List<Message>) -> Unit)? = null
    ) {
        db.collection(EMPLOYEES_TABLE)
            .document(sender)
            .collection(receiver)
            .addSnapshotListener { snapshot, _ ->
                coroutineScope.launch {
                    if (snapshot != null && !snapshot.isEmpty) {
                        val list = snapshot.documents.map {
                            it.toObject(Message::class.java) ?: Message()
                        }
                        val returnList = list.map {
                            Message(
                                it.message.decrypt(getEncryptionKey(sender, receiver)),
                                it.time
                            )
                        }
                        onUpdate?.invoke(returnList)
                    }
                }
            }
    }

    suspend fun chatsListener(
        coroutineScope: CoroutineScope,
        sender: String,
        onUpdate: ((List<Message>) -> Unit)? = null
    ) {
        val receivers = getReceiversForUser(sender)

        receivers.forEach { receiver ->
            db.collection(EMPLOYEES_TABLE)
                .document(receiver)
                .collection(sender)
                .addSnapshotListener { snapshot, _ ->
                    coroutineScope.launch {
                        if (snapshot != null && !snapshot.isEmpty) {
                            val list = snapshot.documents.map {
                                it.toObject(Message::class.java) ?: Message()
                            }
                            val returnList = list.map {
                                Message(
                                    it.message.decrypt(getEncryptionKey(sender, receiver)),
                                    it.time
                                )
                            }
                            onUpdate?.invoke(returnList)
                        }
                    }
                }
        }
    }

    private suspend fun saveEncryptionKey(
        sender: String,
        receiver: String,
        key: String
    ) {
        val idSearch1 = "$sender+$receiver"
        val idSearch2 = "$receiver+$sender"
        db.collection(ENCRYPTION_KEYS_TABLE)
            .document(idSearch1)
            .get()
            .addOnCompleteListener {
                if (!it.result.exists()) {
                    db.collection(ENCRYPTION_KEYS_TABLE)
                        .document(idSearch1)
                        .set(mapOf(KEY to key))
                    db.collection(ENCRYPTION_KEYS_TABLE)
                        .document(idSearch2)
                        .set(mapOf(KEY to key))
                }
            }.await()
    }

    private suspend fun getEncryptionKey(
        sender: String,
        receiver: String
    ): String {
        val idSearch = "$sender+$receiver"
        var key = ""

        db.collection(ENCRYPTION_KEYS_TABLE)
            .get()
            .addOnSuccessListener { result ->
                result.documents.map {
                    if (it.id == idSearch) key = it.get(KEY).toString()
                }
            }.await()

        if (key.isEmpty()) {
            key = getRandomKey()
            saveEncryptionKey(sender, receiver, key)
        }

        return key
    }

    private fun getRandomKey(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..KEY_LENGTH)
            .map { allowedChars.random() }
            .joinToString("")
    }


    companion object {
        private const val EMPLOYEES_TABLE = "Employees"
        private const val USERS_TABLE = "Users"
        private const val ENCRYPTION_KEYS_TABLE = "EncryptionKeys"

        private const val NAME = "name"
        private const val START_DATE = "start_date"
        private const val ROLE = "role"
        private const val SKILL = "skill"
        private const val NORM = "norm"

        private const val TIME = "time"

        private const val KEY_LENGTH = 16
        private const val KEY = "key"
    }
}