import React, { ForwardedRef, ReactNode, forwardRef } from 'react';
import styled from 'styled-components';

interface StyledProgressProps {
  width: string;
  height: string;
}

interface ProgressProps extends StyledProgressProps {
  max: number;
  value: number;
  children: ReactNode;
}

const Progress = forwardRef(function Progress(
  { max, value, children, ...styledProps }: ProgressProps,
  ref?: ForwardedRef<HTMLProgressElement>
) {
  return (
    <StyledProgress ref={ref} max={max} value={ref ? 0 : value} {...styledProps}>
      {children}
    </StyledProgress>
  );
});

const StyledProgress = styled.progress<StyledProgressProps>`
  height: 40px;
`;

export default Progress;
