package net.yuuzu.data.model.customer

interface CustomerDataSource {
    suspend fun getAllCustomers(): List<Customer>
    suspend fun getCustomerByCustomerId(customerId: String): Customer?
    suspend fun getCustomerByCustomerPhone(customerPhone: String): Customer?
    suspend fun insertCustomer(customer: Customer): Boolean
    suspend fun updateCustomer(customer: Customer): Boolean
}