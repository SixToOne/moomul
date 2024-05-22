import React from 'react';
import styled from 'styled-components';

const ImageEditor = () => {
  return (
    <StyledImageEditor>
      <img src="/profile.png" alt="프로필사진" />
    </StyledImageEditor>
  );
};

const StyledImageEditor = styled.div`
  width: 76px;
  height: 76px;

  img {
    width: 100%;
    height: 100%;
    border: 1px solid ${({ theme }) => theme.BORDER_LIGHT};
    border-radius: 100%;
    object-fit: cover;
    overflow: hidden;
  }
`;

export default ImageEditor;
