package net.yuuzu.data.model.customer

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class CustomerDataSourceImpl(
    db: CoroutineDatabase
): CustomerDataSource {
    private val customers = db.getCollection<Customer>()

    override suspend fun getAllCustomers(): List<Customer> {
        return customers.find().toList()
    }

    override suspend fun getCustomerByCustomerId(customerId: String): Customer? {
        return customers.findOneById(customerId)
    }

    override suspend fun getCustomerByCustomerPhone(customerPhone: String): Customer? {
        return customers.findOne(Customer::phone eq customerPhone)
    }

    override suspend fun insertCustomer(customer: Customer): Boolean {
        if (customers.findOne(Customer::phone eq customer.phone) != null) return false
        return customers.insertOne(customer).wasAcknowledged()
    }

    override suspend fun updateCustomer(customer: Customer): Boolean {
        return customers.updateOneById(customer.id, customer).wasAcknowledged()
    }
}