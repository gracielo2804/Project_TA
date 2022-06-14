package com.gracielo.projectta

import com.gracielo.projectta.data.model.AddUsers
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.jakewharton.threetenabp.AndroidThreeTen
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(MockitoJUnitRunner::class)

class RegisterTest {
    val apiServices = ApiServices()
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss")
    val formattedDatetime = current.format(formatter)

    @Test
    fun registerScenario1(){
        val username = "user-$formattedDatetime"
        val password=username
        apiServices.checkUsername(username){
            assertEquals(it?.code,1)
        }
        val userReg= AddUsers(
            function = "adduser",
            id=null,
            username = username,
            password = password,
            email = "cielo.justine01@gmail.com",
            nama = username,
        )
        apiServices.addUser(userReg){
            assertEquals(it?.code,1)
        }
    }

    @Test
    fun registerScenario2(){
        val listUsername = mutableListOf<String>()
        apiServices.getAllUsers {
            it?.dataAllUser?.forEach {data->
               listUsername.add(data.username)
                val username = listUsername[(0..listUsername.size).random()]
                apiServices.checkUsername(username){
                    assertEquals(it?.code,-2)
                    assertEquals(it?.message,"Username Taken")
                }

                val usernamenew = "user-$formattedDatetime"
                apiServices.checkUsername(username){
                    assertEquals(it?.code,1)
                    assertEquals(it?.message,"Username Available")
                }

                val password=usernamenew
                val userReg= AddUsers(
                    function = "adduser",
                    id=null,
                    username = usernamenew,
                    password = password,
                    email = "cielo.justine01@gmail.com",
                    nama = usernamenew,
                )
                apiServices.addUser(userReg){
                    assertEquals(it?.code,1)
                }
            }
        }

    }
}