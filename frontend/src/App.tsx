import { useEffect, useState } from 'react';
import './App.css'

interface ReasearchStudy {
  id: number;
  title: string;
  description: string; 
}

function App() {
  const [studies, setStudies] = useState<ReasearchStudy[]>([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/studies')
    .then((res) => res.json())
    .then((data) => {
      setStudies(data);
    });
  }, []);

  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center">
      <div className="bg-white p-10 rounded-2xl shadow-2xl">
        <h1 className="text-3xl font-extrabold text-indigo-600">
          Haritara Portal: Phase 2 ✅
        </h1>
        <p className="text-gray-500 mt-2">Frontend scaffolded with Vite + Tailwind</p>
        {studies.map((study) => (
          <div key={study.id} className="...">
            <h2>{study.title}</h2>
            <p>{study.description}</p>
            </div>
    ))}
      </div>
    </div>
  )
}

export default App


