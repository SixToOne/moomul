import React from 'react';
import styled from 'styled-components';
import * as icon from '@/assets/icon';

interface StyledProps {
  width?: string;
  height?: string;
}

interface IconProps extends StyledProps {
  name: keyof typeof icon;
}

const Icon = ({ name, ...styledProps }: IconProps) => {
  const SvgIcon = icon[name];
  return <SvgIcon {...styledProps} />;
};

const StyledIcon = styled.div``;

export default Icon;
