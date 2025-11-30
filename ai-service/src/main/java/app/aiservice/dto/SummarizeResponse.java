package app.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for document summarization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizeResponse {
    
    private String summary;
    
    private Long processingTimeMs;
    
    private Boolean cached;
    
    private String modelUsed;
}
