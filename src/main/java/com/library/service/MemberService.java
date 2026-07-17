package com.library.service;

import com.library.dto.request.MemberRequest;
import com.library.dto.request.MemberStatusUpdateRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CreatedResponse;
import com.library.dto.response.MemberResponse;
import com.library.entity.Member;
import com.library.entity.User;
import com.library.enums.MemberStatus;
import com.library.exception.ConflictException;
import com.library.exception.MemberNotFoundException;
import com.library.repository.MemberRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public ApiResponse<CreatedResponse> create(Long userId, MemberRequest request) {
        if (memberRepository.existsByMembershipNumberAndDeletedAtIsNull(request.membershipNumber())) {
            throw new ConflictException("member already exists");
        }
        Member member = Member.builder().fullName(request.fullName())
                .membershipNumber(request.membershipNumber())
                .joinedAt(LocalDate.now()).build();
        member.setCreatedAt(Instant.now());

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("user not found with this id"));
            member.setUser(user);
        }
        memberRepository.save(member);

        return ApiResponse.success(new CreatedResponse(member.getId()));
    }

    public ApiResponse<MemberResponse> getById(Long memberId) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new MemberNotFoundException("member not found with this id"));

        return ApiResponse.success(MemberResponse.fromEntity(member));
    }

    public ApiResponse<Page<MemberResponse>> getAll(String fullName, String status, Pageable pageable) {
        String normalizedFullName = (fullName != null && !fullName.isBlank()) ? fullName.trim() : null;
        MemberStatus memberStatus = parseStatus(status);

        Page<MemberResponse> members = memberRepository
                .search(normalizedFullName, memberStatus, pageable)
                .map(MemberResponse::fromEntity);

        return ApiResponse.success(members);
    }

    private MemberStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return MemberStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Noto'g'ri status qiymati: " + status
                    + ". Mumkin bo'lgan qiymatlar: ACTIVE, SUSPENDED");
        }
    }

    public ApiResponse<Void> update(Long id, MemberRequest request) {
        Member member = memberRepository.findByIdAndDeletedAtIsNullAndStatus(id, MemberStatus.ACTIVE)
                .orElseThrow(() -> new MemberNotFoundException("member not found"));
        member.setFullName(request.fullName());

        if (memberRepository.existsByMembershipNumber(request.membershipNumber())
                && !member.getMembershipNumber().equals(request.membershipNumber())) {
            throw new ConflictException("membership number already exists");
        }
        memberRepository.save(member);

        return ApiResponse.success("successfully updated");
    }

    public ApiResponse<Void> updateStatus(Long id, MemberStatusUpdateRequest request) {

        Member member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException("member not found"));
        member.setStatus(request.status());
        memberRepository.save(member);

        return ApiResponse.success("status has successfully updated");
    }

    public ApiResponse<Void> delete(Long id) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new UsernameNotFoundException("member not found"));

        member.setDeletedAt(Instant.now());
        memberRepository.save(member);

        return ApiResponse.success("Member deleted");
    }
}
