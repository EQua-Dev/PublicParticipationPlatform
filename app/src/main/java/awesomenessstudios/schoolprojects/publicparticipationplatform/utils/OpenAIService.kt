package awesomenessstudios.schoolprojects.publicparticipationplatform.utils

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class OpenAIService(private val apiKey: String) {

    @OptIn(BetaOpenAI::class)
    suspend fun generateCourseDescription(
        subject: String,
        targetGrades: List<String>,
        title: String
    ): String {
        Log.d(this.javaClass.name.toString(), "generateCourseDescription: $apiKey")
        val openAI = OpenAI(apiKey)


        val prompt = """
            Generate a short and engaging course description for a course titled "$title".
            The course covers $subject and is designed for students in ${targetGrades.joinToString(", ")}.
            The description should be concise, professional, and highlight the key benefits of the course.
        """.trimIndent()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"), // Use GPT-3.5 Turbo
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )

        val completion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices.firstOrNull()?.message?.content ?: "Failed to generate description."
    }
}