package pl.sggw.sggwmeet.model.connector.mock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.ReviewSummary
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceListResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData
import retrofit2.Response

class MockPlacesConnector : PlacesConnector {

    override suspend fun getPlaces(category: PlaceCategory?): Response<PlaceListResponse> {

        //mock network delay
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
        }

        return Response.success(
            PlaceListResponse(
                arrayListOf(
                    SimplePlaceResponseData(
                        "937ec1a5-5a94-48d7-8dd7-4fb0c317085e",
                        "Super Knajpa",
                        Geolocation(
                            52.166767,
                            21.049470
                        ),
                        arrayListOf(
                            PlaceCategory.BAR
                        ),
                        "https://wzc.sggw.edu.pl/wp-content/uploads/sites/28/2021/05/kitchen-5055083-scaled.jpg",
                        ReviewSummary(
                            0.67f,
                            123
                        )
                    ),
                    SimplePlaceResponseData(
                        "e714c52a-f2de-4743-9b32-67ce5a0f507a",
                        "Siłka Fest",
                        Geolocation(
                            52.161108,
                            21.037003
                        ),
                        arrayListOf(
                            PlaceCategory.GYM
                        ),
                        "https://justgym.pl/wp-content/uploads/2021/07/justGYM_Warszawa_f-1.jpeg",
                        ReviewSummary(
                            0.12f,
                            26
                        )
                    ),
                    SimplePlaceResponseData(
                        "f50c6241-fbb3-45df-a68b-74703ef75d91",
                        "Potężna Restauracja",
                        Geolocation(
                            52.154793,
                            21.051532
                        ),
                        arrayListOf(
                            PlaceCategory.RESTAURANT
                        ),
                        "https://grandascot.pl/wp-content/uploads/2022/02/Organizacja-uroczystosci-rodzinnych.jpg",
                        ReviewSummary(
                            0.99f,
                            3
                        )
                    ),
                    SimplePlaceResponseData(
                        "eedd6aab-65de-46c6-9f3c-9e85864a8632",
                        "Studencka pijalnia denaturatu",
                        Geolocation(
                            52.142950,
                            21.011719
                        ),
                        arrayListOf(
                            PlaceCategory.OTHER
                        ),
                        "https://d-art.ppstatic.pl/kadry/k/r/1/de/f1/55ad624be52af_o_medium.jpg",
                        ReviewSummary(
                            1.00f,
                            200
                        )
                    ),
                    SimplePlaceResponseData(
                        "18232abc-2f02-4e38-b6fb-a645fa31cd59",
                        "Najlepsza miejscufka",
                        Geolocation(
                            52.193501,
                            20.965351
                        ),
                        arrayListOf(
                            PlaceCategory.BAR,
                            PlaceCategory.RESTAURANT,
                            PlaceCategory.GYM
                        ),
                        "https://i.pinimg.com/1200x/bb/03/94/bb03945e97d27199a0d4743361ceba23.jpg",
                        ReviewSummary(
                            0.42f,
                            12
                        )
                    )
                )
            )
        )
    }
}