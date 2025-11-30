package app.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for document summarization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizeRequest {
    
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 10, message = "Content must be at least 10 characters")
    private String content;
    
    @NotBlank(message = "Filename cannot be blank")
    private String filename;
    
    @NotNull(message = "Max length cannot be null")
    private Integer maxLength = 500;
}
