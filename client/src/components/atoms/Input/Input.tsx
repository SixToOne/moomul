import React from 'react';
import styled from 'styled-components';

interface InputProps {
  label: string;
  name: string;
  handleInput: React.FormEventHandler<HTMLInputElement>;
  isValid?: boolean;
}

const Input = ({ label, name, handleInput, isValid = true }: InputProps) => {
  return (
    <StyledInputLabel htmlFor="inp" className={`inp ${isValid ? '' : 'invalid'}`}>
      <input
        name={name}
        type="text"
        id="inp"
        placeholder="&nbsp;"
        onInput={(e) => handleInput(e)}
      />
      <span className="label">{label}</span>
    </StyledInputLabel>
  );
};

const StyledInputLabel = styled.label`
  position: relative;
  width: 100%;
  display: grid;
  .label {
    position: absolute;
    top: 20px;
    left: 12px;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.5);
    font-weight: 500;
    transform-origin: 0 0;
    transform: translate3d(0, 0, 0);
    transition: all 0.2s ease;
    pointer-events: none;
  }
  input {
    -webkit-appearance: none;
    appearance: none;
    width: 100%;
    border: 0;
    font-family: inherit;
    padding: 16px 12px 0 12px;
    height: 50px;
    font-size: 14px;
    font-weight: 400;
    box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.3);
    color: #000;
    transition: all 0.15s ease;
    box-shadow: inset 0 -2px 0 'inset 0 -1px 0 rgba(0, 0, 0, 0.3)';
  }
  input:not(:placeholder-shown) + .label {
    color: rgba(0, 0, 0, 0.5);
    transform: translate3d(0, -12px, 0) scale(0.75);
  }
  input:focus {
    outline: none;
    box-shadow: inset 0 -2px 0 ${({ theme }) => theme.PRIMARY};
  }
  input:focus + .label {
    color: black;
    transform: translate3d(0, -12px, 0) scale(0.75);
  }

  &.invalid,
  &.invalid input,
  &.invalid input:focus {
    box-shadow: inset 0 -2px 0 red;
  }
`;

export default Input;
