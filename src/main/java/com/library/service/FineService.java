package com.library.service;

import com.library.dto.request.FineWaiveRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.FineResponse;
import com.library.entity.Fine;
import com.library.enums.FineStatus;
import com.library.exception.BusinessRuleViolationException;
import com.library.exception.ConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.FineRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fineRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional(readOnly = true)
    public ApiResponse<Page<FineResponse>> getAll(Long memberId, String status, Pageable pageable) {
        if (status == null || status.isBlank()) {
            return null;
        }
        FineStatus fineStatus;
        try {
            fineStatus = FineStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Noto'g'ri status: " + status
                    + ". Mumkin: UNPAID, PAID, WAIVED");
        }

        Page<FineResponse> fineResponses = fineRepository.search(memberId, fineStatus, pageable).map(FineResponse::fromEntity);

        return ApiResponse.success(fineResponses);
    }

    @Transactional
    public ApiResponse<FineResponse> pay(Long id) {
        Fine fine = fineRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("fine not found: " + id));

        if (!fine.getStatus().equals(FineStatus.UNPAID)) {
            throw new BusinessRuleViolationException("FINE_ALREADY_PROCESSED",
                    "Bu jarima allaqachon " + fine.getStatus() + " holatida, to'lab bo'lmaydi");
        }

        fine.setStatus(FineStatus.PAID);
        fine.setUpdatedAt(Instant.now());
        fine.setUpdatedBy(customUserDetailsService.getCurrentUser().getUsername());
        fineRepository.save(fine);

        return ApiResponse.success("Jarima to'landi", FineResponse.fromEntity(fine));
    }

    @Transactional
    public ApiResponse<FineResponse> waive(Long id, @Valid FineWaiveRequest request) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine topilmadi: " + id));

        if (fine.getStatus() != FineStatus.UNPAID) {
            throw new BusinessRuleViolationException("FINE_ALREADY_PROCESSED",
                    "Bu jarima allaqachon " + fine.getStatus() + " holatida, kechirib bo'lmaydi");
        }

        fine.setStatus(FineStatus.WAIVED);
        // Asl jarima sababi saqlanadi, kechirish sababi qo'shimcha yoziladi
        // (audit trail — nega ADMIN buni kechirgani ko'rinib turishi kerak 😁).
        fine.setReason(fine.getReason() + " | Kechirildi: " + request.reason());
        fineRepository.save(fine);

        return ApiResponse.success("Jarima kechirildi", FineResponse.fromEntity(fine));
    }
}
