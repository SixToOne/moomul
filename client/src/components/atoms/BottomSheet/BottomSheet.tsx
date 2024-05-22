import { ReactNode } from 'react';
import styled from 'styled-components';

interface BottomSheetProps {
  title?: string;
  children: ReactNode;
  open: boolean;
  closeSheet?: () => void;
}

const BottomSheet = ({ title, children, open, closeSheet }: BottomSheetProps) => {
  return (
    <StyledBottomSheet open={open}>
      <Title>{title}</Title>
      <CloseButton onClick={closeSheet}>X</CloseButton>
      {children}
    </StyledBottomSheet>
  );
};

const StyledBottomSheet = styled.div<{ open: boolean }>`
  ${({ open }) => !open && `display:none;`}
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  min-height: 200px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 16px 16px 0 0;
  background-color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
`;

const Title = styled.div`
  padding: 10px;
  font-weight: 600;
`;

const CloseButton = styled.button`
  position: absolute;
  top: 29px;
  right: 20px;
  font-weight: bold;
`;

export default BottomSheet;
