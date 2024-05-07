import React from 'react';
import { useRecoilValue } from 'recoil';
import { styled } from 'styled-components';
import { Link } from 'react-router-dom';
import userSessionAtom from '@/recoil/atoms/userSession';

const Header = () => {
  const userSession = useRecoilValue(userSessionAtom);

  return (
    <StyledHeader>
      <Link to="/">무물</Link>
      {userSession ? (
        <button
          onClick={() => {
            localStorage.clear();
            window.location.reload();
          }}
        >
          로그아웃
        </button>
      ) : (
        <Link to="/login">로그인</Link>
      )}
    </StyledHeader>
  );
};

const StyledHeader = styled.header`
  position: sticky;
  width: 100%;
  height: 56px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: ${({ theme }) => theme.WHITE};
`;

export default Header;
