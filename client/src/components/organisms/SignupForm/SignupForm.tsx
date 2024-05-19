import React, { useState } from 'react';
import styled from 'styled-components';
import Button from '@/atoms/Button';
import Input from '@/atoms/Input';
import { ISignupForm } from '@/apis/user/user';
import { postSignup } from '@/apis/user/post-signup';
import { checkUsernameAvailability } from '@/apis/user/check-username-availability';
import { useNavigate } from 'react-router-dom';

const SignupForm = () => {
  const navigate = useNavigate();

  const [signupFormData, setSignupFormData] = useState<ISignupForm>({
    username: '',
    password: '',
    nickname: '',
  });

  const getSignupFormValue = (e: React.FormEvent<HTMLInputElement>, name: string) => {
    const inputValue = e.currentTarget.value;
    setSignupFormData((prev) => ({ ...prev, [name]: inputValue }));
  };

  const handleClickSignupButton = async () => {
    const data = await postSignup(signupFormData);
    alert('회원가입 성공:), 로그인 해주세요.');
    navigate('/login');
    // if (data) {
    //   alert('회원가입 성공:), 로그인 해주세요.');
    //   navigate('/login');
    // }
  };

  const handleClickCheckUsernameDuplication = async () => {
    const data = await checkUsernameAvailability({ username: signupFormData.username });
    console.log(data);
  };

  return (
    <StyledSignupForm onSubmit={(e) => e.preventDefault()}>
      <div className="login-form__input-box">
        <div className="login-form__input-username">
          <Input
            label={'아이디'}
            name={'username'}
            handleInput={(e) => getSignupFormValue(e, 'username')}
          />
          <Button content="중복확인" onClick={handleClickCheckUsernameDuplication} width="100px" />
        </div>
        <Input
          label={'패스워드'}
          name={'password'}
          handleInput={(e) => getSignupFormValue(e, 'password')}
        />
        <Input
          label={'닉네임'}
          name={'nickname'}
          handleInput={(e) => getSignupFormValue(e, 'nickname')}
        />
      </div>
      <ButtonWrapper>
        <Button content={'회원가입'} onClick={handleClickSignupButton} />
      </ButtonWrapper>
    </StyledSignupForm>
  );
};

const StyledSignupForm = styled.form`
  display: flex;
  flex-direction: column;
  padding: 10px;

  .login-form__input-box {
    margin-bottom: 32px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .login-form__input-username {
    display: flex;
    gap: 16px;
  }
`;

const ButtonWrapper = styled.div`
  height: 36px;
  button {
    height: 100%;
  }
`;

export default SignupForm;
