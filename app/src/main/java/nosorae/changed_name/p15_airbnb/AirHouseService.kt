package nosorae.changed_name.p15_airbnb

import retrofit2.Call
import retrofit2.http.GET

interface AirHouseService {
    @GET("/v3/5a0782b6-5f49-4761-839e-fe29cbccb201")
    fun getHouseList(): Call<AirHouseDTO>
}