import com.azkz.radikot.CompletedListFile
import com.azkz.radikot.ProgramListFile
import com.azkz.radikot.RadikotHTTPClient
import com.azkz.radikot.RadikotProperties
import com.azkz.radikot.notification.NotificationHandler
import com.azkz.radikot.notification.Notificator
import com.azkz.radikot.notification.NotificatorFactory
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
    // 通知に使うハンドラー
    val notificator: Notificator? = NotificatorFactory.create()
    val notificationHandler = NotificationHandler(notificator)

    try {

        logger.info { "====== Radikot 開始 ======" }

        // ==============================================================
        // 開始通知送信
        // ==============================================================
        notificationHandler.onStart()

        // ==============================================================
        // 番組一覧CSVファイル読み込み
        // ==============================================================
        logger.info { "=== TargetCsvFile 開始 ===" }
        val programListCsvFile = ProgramListFile(File(RadikotProperties.PROGRAM_LIST_CSV_FILE_PATH))
        logger.info { "=== TargetCsvFile 終了 ===" }

        // ==============================================================
        // 完了済み番組一覧ファイル読み込み
        // ==============================================================
        logger.info { "=== CompletedListFile 開始 ===" }
        val completedListFile = CompletedListFile(File(RadikotProperties.COMPLETED_LIST_FILE_PATH))
        logger.info { "=== TargetCsvFile 終了 ===" }

        // ==============================================================
        // radiko認証
        // ==============================================================
        logger.info { "=== Radiko authentication 開始 ===" }
        val client = RadikotHTTPClient()
        client.authenticate()
        val token = client.authToken
        logger.info { "=== Radiko authentication 終了 ===" }

        // ==============================================================
        // ダウンロード
        // TODO 並列処理できるようにしてパフォーマンスを改善したい。
        // ==============================================================
        for (radioProgram in programListCsvFile.radioPrograms) {
            // 既にダウンロードされている番組の場合はスキップする
            if (completedListFile.isCompleted(radioProgram)) {
                logger.info { "=== ${radioProgram.name} 完了済みのためスキップ ===" }
                continue
            }

            logger.info { "=== ${radioProgram.name} download 開始 ===" }
            // ダウンロードする
            client.download(radioProgram)
            // 完了済み番組一覧に追加する
            completedListFile.add(radioProgram)
            logger.info { "=== ${radioProgram.name} download 終了 ===" }
        }

        client.logout()
        client.close()

        logger.info { "====== Radikot 終了 ======" }

        // ==============================================================
        // 正常終了通知送信
        // ==============================================================
        notificationHandler.onNormalFinish()

    } catch (e: Exception) {
        logger.error("====== 予期せぬエラー発生 ======", e)
        // ==============================================================
        // 異常終了通知送信
        // ==============================================================
        notificationHandler.onAbnormalFinish(e)
    }

}