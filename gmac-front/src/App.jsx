import { useState } from "react";
import './App.css';

import {Form} from './components/Form.jsx'


function App() {
  return (
    <div className="flex flex-col min-h-screen">
      <main className="flex flex-1 border">
       <div className="w-1/2 border border-red-500">
          {/* Conteúdo da esquerda */}
        </div>
        <div className="w-1/2 flex justify-center items-center border border-blue-500">
          <div className="p-4 border">
            <h1 className="text-2xl font-bold mb-4">GMAC</h1>
            <Form />
          </div>
        </div>
      </main>
      <footer className="h-24 border mt-auto flex items-center justify-center">
        {/* Conteúdo do footer */}
      </footer>
    </div>
  )
}

export default App;
