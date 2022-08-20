package com.shop.service;


import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional    //로직을 처리하다가 에러가 발생하면, 변경된데이터를 로직을 수행하기 이전 상태로 콜백 시켜준다
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private  final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return  memberRepository.save(member);
    }

  private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null){
            throw new IllegalStateException("이미가입된회원입니다");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String email)throws
            UsernameNotFoundException{
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}