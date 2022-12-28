export default function authHeader(contenType: string) {
    let userJwt = localStorage.getItem('userJwt');
    if (userJwt != null) {
      if (contenType != "") {
        return {
          'Authorization': 'Bearer ' + userJwt,
          'Content-Type': contenType
        };
      }
      return { Authorization: 'Bearer ' + userJwt };
    }
    if (contenType != "") {
      return {
        'Content-Type': contenType
      };
    }
    return {};
    
}