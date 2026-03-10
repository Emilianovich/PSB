package ptystudybuddy.psb.email

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization
import org.springframework.http.HttpStatus
import ptystudybuddy.psb.presentation.DateAndTimeHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SendGridService(
    private val sendGrid: SendGrid,
) {
    private val sender = Email("equipo@ptystudybuddy.dev", "Equipo de PTY Study Buddy")

    private fun sendEmailRequest(mail: Mail) {
        val req = Request()
        req.apply {
            method = Method.POST
            endpoint = "mail/send"
            body = mail.build()
        }
        val res = sendGrid.api(req)
        if (res.statusCode != HttpStatus.ACCEPTED.value()) {
            println("No se pudo enviar el correo")
        }
    }

    private fun emailBuilder(
        template: TemplateId,
        personalization: Personalization,
    ): Mail {
        return Mail().apply {
            from = sender
            templateId = template.id
            addPersonalization(personalization)
        }
    }

    private fun personalizationBuilder(
        recipients: List<Email>,
        data: Map<String, String>,
    ): Personalization {
        return Personalization().apply {
            recipients.forEach {
                addTo(it)
            }
            data.forEach { (key, value) -> addDynamicTemplateData(key, value) }
        }
    }

    fun sendEmailAfterDecision(
        fullName: String,
        tutorEmail: String,
        status: Boolean,
    ) {
        if (status) {
            val mapForApproval = mapOf("tutor_name" to fullName)
            sendEmailRequest(
                emailBuilder(
                    TemplateId.APPROVED_TUTOR,
                    personalizationBuilder(listOf(Email(tutorEmail)), mapForApproval),
                ),
            )
        } else {
            val mapForDisapproval = mapOf("tutor_name" to fullName, "today_date" to DateAndTimeHelper.formatDate(LocalDate.now()))
            sendEmailRequest(
                emailBuilder(
                    TemplateId.DISAPPROVED_TUTOR,
                    personalizationBuilder(listOf(Email(tutorEmail)), mapForDisapproval),
                ),
            )
        }
    }

    fun sendEmailAfterInscription(
        fullName: String,
        studentEmail: String,
        subjectName: String,
        sessionStartTime: LocalTime,
        sessionEndTime: LocalTime,
        sessionDate: LocalDateTime,
        tutorName: String,
    ) {
        val map =
            mapOf(
                "student_name" to fullName,
                "subject_name" to subjectName,
                "start_time" to DateAndTimeHelper.formatTime(sessionStartTime),
                "end_time" to DateAndTimeHelper.formatTime(sessionEndTime),
                "formatted_date" to DateAndTimeHelper.formatDatetime(sessionDate),
                "tutor_name" to tutorName,
            )
        sendEmailRequest(
            emailBuilder(
                TemplateId.INSCRIPTION_NOTICE,
                personalizationBuilder(listOf(Email(studentEmail)), map),
            ),
        )
    }

    fun sendEmailAfterInscriptionCancel(
        studentEmail: String,
        subjectName: String,
        sessionDate: LocalDateTime,
        tutorName: String,
    ) {
        val map =
            mapOf(
                "subject_name" to subjectName,
                "formatted_date" to DateAndTimeHelper.formatDatetime(sessionDate),
                "tutor_name" to tutorName,
            )
        sendEmailRequest(
            emailBuilder(
                TemplateId.INSCRIPTION_CANCELLATION,
                personalizationBuilder(listOf(Email(studentEmail)), map),
            ),
        )
    }

    fun sendEmailAfterSessionCancel(
        rawStudentEmails: List<String>,
        subjectName: String,
        sessionDate: LocalDateTime,
        sessionClassroom: String,
    ) {
        val studentEmails = rawStudentEmails.map { Email(it) }
        val map =
            mapOf(
                "subject_name" to subjectName,
                "formatted_date" to DateAndTimeHelper.formatDatetime(sessionDate),
                "session_classroom" to sessionClassroom,
            )
        sendEmailRequest(
            emailBuilder(
                TemplateId.SESSION_CANCELLATION,
                personalizationBuilder(studentEmails, map),
            ),
        )
    }
}

enum class TemplateId(val id: String) {
    APPROVED_TUTOR("d-07693b58abbb445d9ef0c0247094ca20"),
    DISAPPROVED_TUTOR("d-afd908d6cb75498a98880fc9f9aa172e"),
    INSCRIPTION_NOTICE("d-aa383b9a09d84a8aade9109fbc16e1a2"),
    SESSION_CANCELLATION("d-59e277e2896a43faba1b90908abdd42d"),
    INSCRIPTION_CANCELLATION("d-15a9f088ad9a468388fb7820957c6aae"),
}
