import com.azkz.radikot.RadioProgram
import com.azkz.radikot.Station
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.time.LocalDateTime
import java.util.*

suspend fun main(args: Array<String>) {
    val authKey = "bcd151073c03b352e1ef2fd66c32209da9ca0afa"

    val client = HttpClient(CIO)
    val auth1Response: HttpResponse = client.get("https://radiko.jp/v2/api/auth1") {
        headers {
            append("X-Radiko-App", "pc_html5")
            append("X-Radiko-App-Version", "0.0.1")
            append("X-Radiko-User", "dummy_user")
            append("X-Radiko-Device", "pc")
        }
    }
    val headers = auth1Response.headers

    val token = headers["X-Radiko-AuthToken"]!!
    val keyLength = headers["X-Radiko-KeyLength"]!!.toInt()
    val keyOffset = headers["X-Radiko-KeyOffset"]!!.toInt()

    val partialKeyBeforeEncoded = authKey.substring(keyOffset, keyOffset + keyLength)
    val partialKey = Base64.getEncoder().encodeToString(partialKeyBeforeEncoded.toByteArray())

    val auth2Response: HttpResponse = client.get("https://radiko.jp/v2/api/auth2") {
        headers {
            append("X-Radiko-AuthToken", token)
            append("X-Radiko-KeyLength", keyLength.toString())
            append("X-Radiko-KeyOffset", keyOffset.toString())
            append("X-Radiko-PartialKey", partialKey)
        }
    }

    val program = RadioProgram(
        "爆笑問題カーボーイ",
        LocalDateTime.parse("2021-09-29T01:00:00"),
        LocalDateTime.parse("2021-09-29T03:00:00"),
        Station.TBSラジオ
    )

    program.download(token)

    client.close()

}