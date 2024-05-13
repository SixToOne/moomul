import { createGlobalStyle } from 'styled-components';
import { reset } from 'styled-reset';

const GlobalStyle = createGlobalStyle`
    ${reset}
    * {
        box-sizing: border-box;
        -ms-overflow-style: none;
        scrollbar-width: none;
    }
    *::-webkit-scrollbar {
      display: none;
    }
    a {
      text-decoration: none;
      color: black;
    }
    button {
      cursor: pointer;
      background-color: transparent;
      border: none;
      padding: 0;
    }
    body{
      font-family: 'IBM Plex Sans KR', sans-serif;
    }
`;

export default GlobalStyle;
