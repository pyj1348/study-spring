package hello.hellospring.repository.study;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;

import java.util.*;

public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    // 동시성 문제를 해결할 때는 concurrent hashmap을 써야한다
    private static long sequence = 0L;
    // 키값. atom long 써야 동시성 문제 해결

    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {

        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        // 이부분 공부하기
        return store.values().stream()
                .filter(member -> member.getName().equals(name)).findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear();
    }
}
