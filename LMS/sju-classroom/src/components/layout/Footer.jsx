import React from 'react'

export default function Footer() {
  const year = new Date().getFullYear()
  return (
    <footer className="app-footer">
      <div className="container py-3 text-muted small d-flex justify-content-between">
        <span>&copy; <span>{year}</span> JSU Classroom</span>
        <span>v0.1 Prototype</span>
      </div>
    </footer>
  )
}

