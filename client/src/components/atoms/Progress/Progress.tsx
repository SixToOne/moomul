import useProgress from '@/hooks/useProgress';
import React, { ForwardedRef, forwardRef } from 'react';
import styled from 'styled-components';

interface StyledProps {
  color: 'primary' | 'gray';
}

interface ProgressProps extends StyledProps {
  max: number;
  value: number;
  content: string;
}

const Progress = ({ max, value, content, ...styledProps }: ProgressProps) => {
  const ref = useProgress({ initialValue: 0, value, incrementMod: 6 });

  return (
    <StyledProgress {...styledProps}>
      <progress ref={ref} max={max} value={ref ? 0 : value} />
      <span className="progress__text">{content}</span>
      <span className="progress__value">{value}%</span>
    </StyledProgress>
  );
};

const StyledProgress = styled.div<StyledProps>`
  position: relative;
  cursor: pointer;

  progress {
    width: 100%;
    height: 44px;
    border-radius: 4px;
  }

  progress::-webkit-progress-bar {
    padding: 2px;
    background-color: ${({ theme }) => theme.WHITE};
    border: 1px solid
      ${({ theme, color }) => (color === 'primary' ? theme.PRIMARY : theme.BACKGROUND_GRAY)};
    border-radius: 4px;
  }
  progress::-webkit-progress-value {
    background-color: ${({ theme, color }) =>
      color === 'primary' ? theme.PRIMARY_LIGHT : theme.BACKGROUND_GRAY};
    border-radius: 3px;
  }

  span {
    position: absolute;
    top: 15px;
    color: ${({ theme, color }) => (color === 'primary' ? theme.PRIMARY : theme.LIGHT_BLACK)};
    font-size: 14px;
    ${({ color }) => color === 'primary' && 'font-weight: 600'};
  }
  .progress__text {
    left: 14px;
  }
  .progress__value {
    right: 14px;
  }
`;

export default Progress;
