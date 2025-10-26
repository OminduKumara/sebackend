package com.backend.mybungalow.faq;

import com.backend.mybungalow.faq.dto.CreateFAQRequest;
import com.backend.mybungalow.faq.dto.CreateFAQRequest;
import com.backend.mybungalow.faq.dto.FAQResponse;
import com.backend.mybungalow.faq.dto.FAQResponse;
import com.backend.mybungalow.faq.dto.UpdateFAQRequest;
import com.backend.mybungalow.faq.dto.UpdateFAQRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FAQService {
    private final FaqRepository faqRepository;

    public FAQService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FAQResponse> getPublicFaqs() {
        return faqRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public List<FAQResponse> getAllFaqs() {
        return faqRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public FAQResponse createFaq(CreateFAQRequest request) {
        FAQ faq = new FAQ();
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setCategory(request.category());
        faq.setIsActive(request.isActive());
        faq.setDisplayOrder(request.displayOrder());
        return map(faqRepository.save(faq));
    }

    @Transactional
    public FAQResponse updateFaq(Long id, UpdateFAQRequest request) {
        FAQ faq = faqRepository.findById(id).orElseThrow(() -> new RuntimeException("FAQ not found"));
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setCategory(request.category());
        faq.setIsActive(request.isActive());
        faq.setDisplayOrder(request.displayOrder());
        return map(faqRepository.save(faq));
    }

    @Transactional
    public void deleteFaq(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new RuntimeException("FAQ not found");
        }
        faqRepository.deleteById(id);
    }

    private FAQResponse map(FAQ faq) {
        return new FAQResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCategory(),
                faq.getIsActive(),
                faq.getDisplayOrder(),
                faq.getCreatedAt()
        );
    }
}

 