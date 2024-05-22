import axios from 'axios';

const path = '/users/id-check';

interface ReturnType {
  isValid: boolean;
}

export const checkUsernameAvailability = async (data: { username: string }) => {
  const res = await axios.post<ReturnType>(`${path}`, data);
  return res.data;
};
