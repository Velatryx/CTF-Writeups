## Room Description: Can you bypass the engine?

---

> It is a very simple CTF. We want to back rank checkmate the black king by rook a1 to a8, however, the engine refuses to let us do so. 

We want to find a way to bypass the js blocking us: 

<img width="3456" height="1695" alt="Screenshot From 2026-07-07 17-17-56" src="https://github.com/user-attachments/assets/b9c8ad7a-902b-4f6d-ac1e-fbf5f09495d1" />

---
Then I fired up burpsuite to communicate directly with api withous .js interfering.

<img width="3155" height="1809" alt="Screenshot From 2026-07-07 17-17-13" src="https://github.com/user-attachments/assets/0c3f3081-4c2c-4b81-ba2b-3fcff3bc1121" />

We checkmate the king, and receive the flag.
