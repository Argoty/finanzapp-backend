package com.finanzapp.app_financiera.services;

import com.finanzapp.app_financiera.models.User;
import com.finanzapp.app_financiera.configGson.LocalDateAdapter;
import com.finanzapp.app_financiera.configGson.LocalDateTimeAdapter;
import com.finanzapp.app_financiera.repository.DebtRepository;
import com.finanzapp.app_financiera.repository.PlannedPaymentRepository;
import com.finanzapp.app_financiera.repository.RecordRepository;
import com.finanzapp.app_financiera.repository.SavingRepository;
import com.finanzapp.app_financiera.repository.UserRepository;
import com.finanzapp.app_financiera.security.JwtUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class GeminiService {

    private final RecordRepository recordRepository;
    private final BudgetService budgetService;
    private final PlannedPaymentRepository plannedPaymentRepository;
    private final DebtRepository debtRepository;
    private final SavingRepository savingRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("APIGEMINI_KEY");
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private static final String menu = """
                                       ENTIDADES: 1.registro, movimiento
                                       2.gasto
                                       3.ingreso
                                       4.presupuesto
                                       5.recordatorio de pago, pago planificado
                                       6.ahorro, objetivo, plan
                                       7.deuda, debito
                                       8.Ninguna de las anteriores""";

    @Autowired
    public GeminiService(RecordRepository rr, BudgetService bs, PlannedPaymentRepository ppr, DebtRepository dr, SavingRepository sr, UserRepository ur, JwtUtil jwtUtil) {
        recordRepository = rr;
        budgetService = bs;
        plannedPaymentRepository = ppr;
        debtRepository = dr;
        savingRepository = sr;
        userRepository = ur;
        this.jwtUtil = jwtUtil;
    }

    public String generateContent(String prompt) {
        JsonObject requestBody = new JsonObject();
        JsonObject content = new JsonObject();
        JsonObject part = new JsonObject();
        part.addProperty("text", prompt);
        content.add("parts", gson.toJsonTree(new JsonObject[]{part}));
        requestBody.add("contents", gson.toJsonTree(new JsonObject[]{content}));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObj = new JSONObject(response.body());
            System.out.print(jsonObj);

            return jsonObj.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error al generar contenido: " + e.getMessage();
        }
    }

    public String handleGeminiRequest(String token, String userPrompt) {
        String email = jwtUtil.extractEmailFromAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        int userId = user.getId();

        LocalDate ahora = LocalDate.now();
        String fullPrompt = "MENÚ: 1.registro, movimiento | 2.gasto | 3.ingreso | 4.presupuesto | 5.recordatorio | 6.ahorro | 7.deuda | 0.ninguna. Dado el siguiente mensaje del usuario: '"
                + userPrompt + "'. Responde solo con el número de la opción que más se relacione.";

        String responseMenuOption = generateContent(fullPrompt);
        int nro = Integer.parseInt(String.valueOf(responseMenuOption.charAt(0)));

        String datosDeBD = "";
        if (nro == 1) {
            datosDeBD = "Registros del usuario: " + gson.toJson(recordRepository.findByUserId(userId));
        } else if (nro == 2) {
            datosDeBD = "Gastos del usuario: " + gson.toJson(recordRepository.findAllByUserIdAndType(userId, "Gasto"));
        } else if (nro == 3) {
            datosDeBD = "Ingresos del usuario: " + gson.toJson(recordRepository.findAllByUserIdAndType(userId, "Ingreso"));
        } else if (nro == 4) {
            datosDeBD = "Presupuestos del usuario: " + gson.toJson(budgetService.getAllBudgetsStatus(token, null));
        } else if (nro == 5) {
            datosDeBD = "Recordatorio de pagos del usuario: " + gson.toJson(plannedPaymentRepository.findByUserId(userId));
        } else if (nro == 6) {
            datosDeBD = "Ahorros del usuario: " + gson.toJson(savingRepository.findAllByUserId(userId));
        } else if (nro == 7) {
            datosDeBD = "Deudas del usuario: " + gson.toJson(debtRepository.findAllByUserId(userId));
        } else {
            datosDeBD = "No se encontraron datos relacionados para la opción " + nro;
        }
        String finalPrompt = "Genera una respuesta útil y resumida. Dado el siguiente mensaje del usuario: '" + userPrompt + "' y los siguientes datos:"
                + datosDeBD + ". fecha de hoy: " + ahora;

        String respuestaFinal = generateContent(finalPrompt);
        return respuestaFinal;

    }
}
