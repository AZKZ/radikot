import com.azkz.radikot.ProgramListCsvFile
import com.azkz.radikot.RadikoAuthHTTPClient
import com.azkz.radikot.RadikotProperties
import mu.KotlinLogging
import java.io.File

/**
 * ロガー
 */
private val logger = KotlinLogging.logger {}

/**
 * radikotのエントリーポイントです。<br>
 */
suspend fun main() {

    try {

        logger.info { "====== Radikot 開始 ======" }

        // ==============================================================
        // 番組一覧CSVファイル読み込み
        // ==============================================================
        logger.info { "=== TargetCsvFile 開始 ===" }
        val programListCsvFile = ProgramListCsvFile(File(RadikotProperties.PROGRAM_LIST_CSV_FILE_PATH))
        logger.info { "=== TargetCsvFile 終了 ===" }

        // ==============================================================
        // radiko認証
        // ==============================================================
        logger.info { "=== Radiko authentication 開始 ===" }
        val client = RadikoAuthHTTPClient()
        client.authenticate()
        val token = client.authToken
        logger.info { "=== Radiko authentication 終了 ===" }

        // ==============================================================
        // ダウンロード
        // TODO 並列処理できるようにしてパフォーマンスを改善したい。
        // ==============================================================
        for (radioProgram in programListCsvFile.radioPrograms) {
            logger.info { "=== ${radioProgram.name} download 開始 ===" }
            client.download(radioProgram)
            logger.info { "=== ${radioProgram.name} download 終了 ===" }
        }

        client.logout()
        client.close()

        logger.info { "====== Radikot 終了 ======" }

    } catch (e: Exception) {
        logger.error("====== 予期せぬエラー発生 ======", e)
    }

}