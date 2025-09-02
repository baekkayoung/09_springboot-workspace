package com.kh.spring.member.model.dao;

import com.kh.spring.member.model.vo.Member;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDao {

    public Member loginMember(SqlSessionTemplate sqlSession, Member m) {

        return sqlSession.selectOne("memberMapper.loginMember" , m);

    }


    public int insertMember(SqlSessionTemplate sqlSession, Member m) {
        return sqlSession.insert("memberMapper.insertMember", m);
    }



    public int updateMemer(SqlSessionTemplate sqlSession, Member m) {
        return sqlSession.insert("memberMapper.updateMemer", m);
    }


    public int deleteMember(SqlSessionTemplate sqlSession, String userId) {
        return sqlSession.update("memberMapper.deleteMember", userId);
    }


    public int idCheck(SqlSessionTemplate sqlSession, String checkId) {
        return sqlSession.selectOne("memberMapper.idCheck", checkId);

    }
}
