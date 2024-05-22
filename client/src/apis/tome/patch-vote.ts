import axios from 'axios';
import { IToMe } from './tome';

const path = `/tome`;

export const patchVote = async (username: string, tomeId: number, optionId: number) => {
  const res = await axios.patch<IToMe>(`${path}/${tomeId}/votes?username=${username}`, {
    voted: optionId,
  });
  return res.data;
};
