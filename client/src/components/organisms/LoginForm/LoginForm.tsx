import React, { useState } from 'react';
import styled from 'styled-components';
import axios from 'axios';
import Button from '@/atoms/Button';
import Input from '@/atoms/Input';
import { ILoginForm } from '@/apis/user/user';
import { postLogin } from '@/apis/user/post-login';
import { useNavigate } from 'react-router-dom';
import { useRecoilState } from 'recoil';
import userSessionAtom from '@/recoil/atoms/userSession';

const LoginForm = () => {
  const navigate = useNavigate();
  const [userSession, setUserSession] = useRecoilState(userSessionAtom);

  const [loginFormData, setLoginFormData] = useState<ILoginForm>({
    username: '',
    password: '',
  });

  const getLoginFormValue = (e: React.FormEvent<HTMLInputElement>, name: string) => {
    const inputValue = e.currentTarget.value;
    setLoginFormData((prev) => ({ ...prev, [name]: inputValue }));
  };

  const handleClickLoginButton = async () => {
    const data = await postLogin(loginFormData);
    if (data) {
      const { userId, accessToken } = data;
      axios.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken;
      localStorage.setItem('accessToken', accessToken);
      setUserSession({ userId });
      alert(`로그인 성공`);
      navigate('/');
    } else {
      alert(`로그인 실패`);
    }
  };

  return (
    <StyledLoginForm onSubmit={(e) => e.preventDefault()}>
      <div className="login-form__input-box">
        <Input
          label={'아이디'}
          name={'username'}
          handleInput={(e) => getLoginFormValue(e, 'username')}
        />
        <Input
          label={'패스워드'}
          name={'password'}
          handleInput={(e) => getLoginFormValue(e, 'password')}
        />
      </div>
      <Button content={'로그인'} onClick={handleClickLoginButton} />
    </StyledLoginForm>
  );
};

const StyledLoginForm = styled.form`
  display: flex;
  flex-direction: column;
  padding: 10px;

  .login-form__input-box {
    margin-bottom: 32px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
`;

export default LoginForm;
