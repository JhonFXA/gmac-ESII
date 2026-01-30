import '';

import logo from "../assets/gmac-logo.png";
import illustration from "../assets/illustration.svg";
import {Form} from '../components/Form.jsx';
import Footer from '../components/Footer.jsx';


function Login() {
  return (
    <div className="container">

        <main className="">
            {/* Conteúdo da esquerda */}
            <section className="image-content">
                <img src={illustration} height={500} alt="" />
            </section>

            <section className="form-content">
                <div className="form-div">
                <img src={logo} height={200}/>
                <Form />
                </div>
            </section>
        </main>

        <Footer />
    </div>
  )
}

export default Login;
