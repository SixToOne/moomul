import React from 'react';
import { useRecoilValue } from 'recoil';
import { Link, Navigate } from 'react-router-dom';
import LoginForm from '@/organisms/LoginForm';
import userSessionAtom from '@/recoil/atoms/userSession';

const Login = () => {
  const userSession = useRecoilValue(userSessionAtom);

  if (userSession) return <Navigate to="/" />;

  return (
    <>
      <LoginForm />
      <Link to="/signup">회원가입</Link>
    </>
  );
};

export default Login;