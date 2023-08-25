package net.yuuzu.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.yuuzu.data.model.customer.Customer
import net.yuuzu.data.model.customer.CustomerDataSource
import net.yuuzu.data.request.CustomerRequest

fun Route.customer(
    customerDataSource: CustomerDataSource // 你的資料源，用於與資料庫交互
) {
    authenticate {
        // 創建新客戶
        post("/customer") {
            val request = call.receive<CustomerRequest>() // 從請求主體中接收客戶資料

            // 檢查客戶資料是否為空
            val areFieldsBlank = request.name.isBlank() ||
                    request.phone.isBlank() ||
                    request.email.isBlank() ||
                    request.address.isBlank()

            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict, "Customer info is blank")
                return@post
            }

            // 創建客戶
            val customer = Customer(
                name = request.name,
                phone = request.phone,
                email = request.email,
                address = request.address
            )

            val wasAcknowledged = customerDataSource.insertCustomer(customer)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict, "Customer already exists")
                return@post
            }

            call.respond(HttpStatusCode.OK)
        }

        // 讀取所有客戶
        get("/customer") {
            val customers = customerDataSource.getAllCustomers()
            call.respond(HttpStatusCode.OK, customers)
        }

        // 讀取特定客戶
        get("/customer/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@get
            }
            val customer = customerDataSource.getCustomerByCustomerId(id)
            if (customer != null) {
                call.respond(HttpStatusCode.OK, customer)
            } else {
                call.respond(HttpStatusCode.NotFound, "Customer not found")
            }
        }

        // 更新特定客戶
        put("/customer/{id}") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Missing customer ID")
                return@put
            }
            val updatedCustomer = call.receive<CustomerRequest>()

            val currentCustomer = customerDataSource.getCustomerByCustomerId(id)
            if (currentCustomer == null) {
                call.respond(HttpStatusCode.NotFound, "Customer not found")
                return@put
            }

            val customer = Customer(
                id = currentCustomer.id,
                name = updatedCustomer.name.ifBlank { currentCustomer.name },
                phone = updatedCustomer.phone.ifBlank { currentCustomer.phone },
                email = updatedCustomer.email.ifBlank { currentCustomer.email },
                address = updatedCustomer.address.ifBlank { currentCustomer.address }
            )

            val result = customerDataSource.updateCustomer(customer = customer)
            if (result) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Customer not found")
            }
        }
    }
}
