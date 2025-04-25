import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import AuthUser from "./AuthUser";
import AuthStaff from "./AuthStaff";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/auth-user" element={<AuthUser />} />
        <Route path="/auth-staff" element={<AuthStaff />} />
      </Routes>
    </Router>
  );
}

export default App;
