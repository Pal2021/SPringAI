package com.spring_ai_tools.spring_ai_tools_calling.Tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeTools {

    @Tool(description = "Get the current date and time in a specific timezone")
    public String getCurrentTime(
            @ToolParam(description = "Timezone ID (e.g., 'Asia/Kolkata', 'America/New_York')")
            String timezone
    ) {
        System.out.println( "tool has been called with timezone: " + timezone);
        try {
            System.out.println( "tool has been called with timezone: " + timezone);
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            String formatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
            System.out.println("[TOOL CALLED] getCurrentTime → timezone=" + timezone + " → " + formatted);
            return "Current time in " + timezone + " is: " + formatted;
        } catch (Exception e) {
            return "Invalid timezone: " + timezone + ". Please use a valid TZ ID like 'Asia/Kolkata'.";
        }
    }
}