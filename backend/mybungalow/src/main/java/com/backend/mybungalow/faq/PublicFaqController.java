    package com.backend.mybungalow.faq;

    import com.backend.mybungalow.faq.dto.FAQResponse;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.List;

    @RestController
    @RequestMapping("/api/FAQs")
    public class PublicFaqController {
        private final FAQService faqService;

        public PublicFaqController(FAQService faqService) {
            this.faqService = faqService;
        }

        @GetMapping
        public ResponseEntity<List<FAQResponse>> getFaqs() {
            return ResponseEntity.ok(faqService.getPublicFaqs());
        }
    }


