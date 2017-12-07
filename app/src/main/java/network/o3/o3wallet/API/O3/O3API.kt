package network.o3.o3wallet.API.O3

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.core.FuelManager
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import network.o3.o3wallet.API.CoZ.Claims
import network.o3.o3wallet.API.CoZ.CoZClient
import network.o3.o3wallet.API.CoZ.TransactionHistory

/**
 * Created by drei on 11/24/17.
 */

class O3API {
    val baseURL = "https://staging-api.o3.network/v1/"
    enum class Route() {
        HISTORY,
        PORTFOLIO;

        fun routeName(): String {
            return this.name.toLowerCase()
        }
    }

    fun getPriceHistory(symbol: String, interval: Int, completion: (Pair<PriceHistory?, Error?>) -> (Unit)) {
        val url = baseURL + Route.HISTORY.routeName() + "/" + symbol + String.format("?i=%d", interval)
        var request = url.httpGet()
        request.responseString { request, response, result ->
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val o3Response = gson.fromJson<O3Response>(data!!)
                println(o3Response.result["data"])
                val history = gson.fromJson<PriceHistory>(o3Response.result["data"])
                completion(Pair<PriceHistory?, Error?>(history, null))
            } else {
                completion(Pair<PriceHistory?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun getPortfolio(neoAmount: Int, gasAmount: Double, interval: Int, completion: (Pair<Portfolio?, Error?>) -> Unit) {
        val url = baseURL + Route.PORTFOLIO.routeName() + String.format("?i=%d&neo=%d&gas=%f", interval, neoAmount, gasAmount)
        var request = url.httpGet()
        request.responseString { request, response, result ->
           // print (request)
           // print (response)
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val o3Response = gson.fromJson<O3Response>(data!!)
                println(o3Response.result["data"])
                val history = gson.fromJson<Portfolio>(o3Response.result["data"])
                completion(Pair<Portfolio?, Error?>(history, null))
                FuelManager.instance.executor.shutdown()

            } else {
                completion(Pair<Portfolio?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }
}