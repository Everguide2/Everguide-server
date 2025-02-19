package com.example.everguide.service.auth;

import com.example.everguide.domain.Member;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.auth.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        Optional<Member> memberData = memberRepository.findByUserId(userId);

        if (memberData.isPresent()) {

            return new CustomUserDetails(memberData.get());
        }

        return null;
    }
}
