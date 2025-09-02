package com.kh.spring.member.model.service;

import com.kh.spring.member.model.dao.MemberDao;
import com.kh.spring.member.model.vo.Member;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService{

    @Autowired
    private MemberDao mDao;

    @Autowired
    private SqlSessionTemplate sqlSession;


    @Override
    public Member loginMember(Member m) {

        return mDao.loginMember(sqlSession, m);

    }

    @Override
    public int insertMember(Member m) {
        return mDao.insertMember(sqlSession ,m);
    }

    @Override
    public int updateMember(Member m) {
        return mDao.updateMemer(sqlSession, m);
    }

    @Override
    public int deleteMember(String userId) {
        return mDao.deleteMember(sqlSession, userId);
    }

    @Override
    public int idCheck(String checkId) {
        return mDao.idCheck(sqlSession, checkId);
    }

}
