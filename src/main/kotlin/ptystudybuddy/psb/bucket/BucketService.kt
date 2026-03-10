package ptystudybuddy.psb.bucket

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class BucketService(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
) {
    @Value(value = "\${bucket.name}")
    lateinit var bucketName: String

    val waiterDuration = 30L
    val signedUrlDuration = 3L

    fun upload(file: MultipartFile): String {
        try {

            val extension = file.originalFilename
                ?.substringAfterLast('.', "")
                ?.takeIf { it.isNotBlank() }

            val newKey = if (extension != null) {
                "${UUID.randomUUID()}.$extension"
            } else {
                UUID.randomUUID().toString()
            }

            val fileUploadRequest =
                PutObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(newKey)
                    .contentType(file.contentType)
                    .build()

            s3Client.putObject(
                fileUploadRequest,
                RequestBody.fromBytes(file.bytes),
            )

            this.s3Client.waiter().waitUntilObjectExists(
                {
                    it.bucket(bucketName)
                        .key(newKey)
                },
                { it.waitTimeout(Duration.ofSeconds(waiterDuration)) },
            )

            return newKey
        } catch (e: S3Exception) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "File Upload Failed: ${e.message}",
                e,
            )
        }
    }

    fun delete(fileKey: String): Boolean {
        try {
            val deleteObjectRequest =
                DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build()

            this.s3Client.deleteObject(deleteObjectRequest)
            this.s3Client.waiter().waitUntilObjectNotExists(
                {
                    it.bucket(bucketName)
                        .key(fileKey)
                },
                { it.waitTimeout(Duration.ofSeconds(waiterDuration)) },
            )

            return true
        } catch (e: S3Exception) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "File Delete Failed: ${e.message}",
                e,
            )
        }
    }

    fun update(
        file: MultipartFile,
        fileKey: String,
    ): String {

        this.delete(fileKey)
        return this.upload(file)
    }

    fun getSignedUrl(fileKey: String): String {
        try {
            val fileUpload =
                GetObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build()

            val urlSigned =
                GetObjectPresignRequest
                    .builder()
                    .signatureDuration(Duration.ofHours(signedUrlDuration))
                    .getObjectRequest(fileUpload)
                    .build()

            val presignedObject = s3Presigner.presignGetObject(urlSigned)

            return presignedObject.url().toString()
        } catch (e: S3Exception) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "File Get Signed Url Failed: ${e.message}",
                e,
            )
        }
    }
}
