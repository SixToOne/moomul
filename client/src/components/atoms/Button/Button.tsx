import React from 'react';
import styled from 'styled-components';

interface StyledProps {
  width?: string;
  height?: string;
  fontSize?: string;
}

interface ButtonProps extends StyledProps {
  content: string;
  onClick: React.MouseEventHandler<HTMLButtonElement>;
}

const Button = ({ content, onClick, ...styledProps }: ButtonProps) => {
  return (
    <StyledButton onClick={onClick} {...styledProps}>
      {content}
    </StyledButton>
  );
};

const StyledButton = styled.button<StyledProps>`
  width: ${({ width }) => width ?? '100%'};
  padding: 5px 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: ${({ theme }) => theme.PRIMARY};
  color: ${({ theme }) => theme.WHITE};
  font-size: ${({ fontSize }) => fontSize ?? '16px'};
  font-weight: 500;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: ${({ theme }) => theme.PRIMARY_DARK};
  }
`;

export default Button;
