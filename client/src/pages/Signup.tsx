import React from 'react';
import { useRecoilValue } from 'recoil';
import { Link, Navigate } from 'react-router-dom';
import SignupForm from '@/organisms/SignupForm';
import userSessionAtom from '@/recoil/atoms/userSession';

const Signup = () => {
  const userSession = useRecoilValue(userSessionAtom);

  if (userSession) return <Navigate to="/" />;

  return (
    <>
      <SignupForm />
    </>
  );
};

export default Signup;
