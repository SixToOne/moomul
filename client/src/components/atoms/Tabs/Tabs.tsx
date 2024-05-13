import styled from 'styled-components';

export interface ITab {
  name: string;
  selected: boolean;
  handleClick: () => void;
}

interface TabsProps {
  tabs: ITab[];
}

const Tabs = ({ tabs }: TabsProps) => {
  return (
    <StyledTabs>
      {tabs.map((tab, index) => (
        <Tab selected={tab.selected} onClick={tab.handleClick} key={`tab_${index}`}>
          {tab.name}
        </Tab>
      ))}
    </StyledTabs>
  );
};

const StyledTabs = styled.div`
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-around;
  border-radius: 0.5em;
`;

const Tab = styled.span<{ selected: boolean }>`
  width: 100%;
  padding: 12px 8px;
  text-align: center;
  font-size: 16px;
  font-weight: ${({ selected }) => (selected ? 600 : 400)};
  border-bottom: ${({ theme, selected }) => (selected ? `2px solid ${theme.PRIMARY}` : 'white')};
  color: ${({ theme, selected }) => (selected ? theme.DARK_BLACK : theme.LIGHT_BLACK)};
  cursor: pointer;
`;

export default Tabs;
