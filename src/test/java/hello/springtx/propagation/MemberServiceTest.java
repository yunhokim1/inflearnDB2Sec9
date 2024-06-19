package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON
     */
    @Test
    void outerTxOff_success() {
        //given
        String userName = "outerTxOff_success";

        //when
        memberService.joinV1(userName);

        //then
        assertTrue(memberRepository.find(userName).isPresent());
    }

    /**
     * memberService    @Transactional: OFF
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */
    @Test
    void outerTxOff_fail() {
        //given
        String userName = "로그예외outerTxOff_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(userName))
                .isInstanceOf(RuntimeException.class);


        //then
        assertTrue(memberRepository.find(userName).isPresent());
        assertTrue(logRepository.find(userName).isEmpty());
    }

    /**
     * memberService    @Transactional: On
     * memberRepository @Transactional: OFF
     * logRepository    @Transactional: OFF
     */
    @Test
    void singleTx() {
        //given
        String userName = "singleTx ";

        //when
        memberService.joinV1(userName);

        //then
        assertTrue(memberRepository.find(userName).isPresent());
    }

    /**
     * memberService    @Transactional: On
     * memberRepository @Transactional: On
     * logRepository    @Transactional: On
     */
    @Test
    void outerTxOn_success() {
        //given
        String userName = "outerTxOn_success";

        //when
        memberService.joinV1(userName);

        //then
        assertTrue(memberRepository.find(userName).isPresent());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */
    @Test
    void outerTxOn_fail() {
        //given
        String userName = "로그예외outerTxOn_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV1(userName))
                .isInstanceOf(RuntimeException.class);


        //then
        assertTrue(memberRepository.find(userName).isEmpty());
        assertTrue(logRepository.find(userName).isEmpty());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        String userName = "로그예외recoverException_fail";

        //when
        assertThatThrownBy(() -> memberService.joinV2(userName))
                .isInstanceOf(UnexpectedRollbackException.class);


        //then
        assertTrue(memberRepository.find(userName).isEmpty());
        assertTrue(logRepository.find(userName).isEmpty());
    }

    /**
     * memberService    @Transactional: ON
     * memberRepository @Transactional: ON
     * logRepository    @Transactional: ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String userName = "로그예외recoverException_success";

        //when
        memberService.joinV2(userName);


        //then: member 저장, log 롤백
        assertTrue(memberRepository.find(userName).isPresent());
        assertTrue(logRepository.find(userName).isEmpty());
    }

}