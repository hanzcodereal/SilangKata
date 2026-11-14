#!/usr/bin/env python3
"""Memeriksa file level Silang Kata.

Aturan yang dicek untuk setiap level:
- setiap kata minimal 2 huruf dan hanya berisi A-Z;
- kata berada di dalam kotak level (rows x cols);
- huruf yang berpotongan harus sama;
- setiap deretan huruf mendatar/tegak yang panjangnya >= 2 harus tepat sama
  dengan satu kata, sehingga tidak ada kata tak sengaja;
- setiap kotak berisi huruf dilewati oleh minimal satu kata.

Pemakaian: python3 tools/validate_levels.py app/src/main/assets/levels.json
"""
import json
import sys


def build(words):
    """Mengubah daftar kata menjadi peta kotak. Mengembalikan (cells, error)."""
    cells = {}
    for w in words:
        ans = w["answer"]
        for i, ch in enumerate(ans):
            r = w["row"] + (0 if w["across"] else i)
            c = w["col"] + (i if w["across"] else 0)
            if (r, c) in cells and cells[(r, c)] != ch:
                return None, f"huruf bertabrakan di baris {r}, kolom {c} ({ans})"
            cells[(r, c)] = ch
    return cells, None


def find_problem(words, rows, cols):
    """Mengembalikan pesan kesalahan pertama, atau None jika level valid."""
    for w in words:
        if not w["answer"].isalpha() or not w["answer"].isascii() or len(w["answer"]) < 2:
            return f"kata tidak valid: {w['answer']!r}"
        if not w["answer"].isupper():
            return f"kata harus huruf besar: {w['answer']!r}"
        if not w.get("clue", "").strip():
            return f"petunjuk kosong untuk {w['answer']}"
        end_r = w["row"] + (0 if w["across"] else len(w["answer"]) - 1)
        end_c = w["col"] + (len(w["answer"]) - 1 if w["across"] else 0)
        if w["row"] < 0 or w["col"] < 0 or end_r >= rows or end_c >= cols:
            return f"kata {w['answer']} keluar dari kotak {rows}x{cols}"

    if len({w["answer"] for w in words}) != len(words):
        return "ada kata yang dipakai dua kali"

    cells, err = build(words)
    if err:
        return err

    across_starts = {(w["row"], w["col"]): len(w["answer"]) for w in words if w["across"]}
    down_starts = {(w["row"], w["col"]): len(w["answer"]) for w in words if not w["across"]}

    for r in range(rows):
        c = 0
        while c < cols:
            if (r, c) in cells:
                start = c
                while c + 1 < cols and (r, c + 1) in cells:
                    c += 1
                length = c - start + 1
                if length >= 2 and across_starts.get((r, start)) != length:
                    return f"deretan mendatar tak sengaja di baris {r}, kolom {start}"
            c += 1

    for c in range(cols):
        r = 0
        while r < rows:
            if (r, c) in cells:
                start = r
                while r + 1 < rows and (r + 1, c) in cells:
                    r += 1
                length = r - start + 1
                if length >= 2 and down_starts.get((start, c)) != length:
                    return f"deretan tegak tak sengaja di kolom {c}, baris {start}"
            r += 1

    covered = set()
    for w in words:
        for i in range(len(w["answer"])):
            covered.add((w["row"] + (0 if w["across"] else i), w["col"] + (i if w["across"] else 0)))
    if set(cells) - covered:
        return "ada huruf yang tidak termasuk kata mana pun"
    return None


def validate_file(path):
    with open(path, encoding="utf-8") as f:
        data = json.load(f)
    levels = data["levels"]
    ids = [lv["id"] for lv in levels]
    if len(set(ids)) != len(ids):
        return ["id level tidak unik"]
    problems = []
    for lv in levels:
        err = find_problem(lv["words"], lv["rows"], lv["cols"])
        if err:
            problems.append(f"Level {lv['id']} ({lv['title']}): {err}")
    return problems, levels


def main():
    if len(sys.argv) != 2:
        print("Pemakaian: validate_levels.py <levels.json>")
        return 2
    result = validate_file(sys.argv[1])
    if isinstance(result, list):
        for p in result:
            print("GAGAL:", p)
        return 1
    problems, levels = result
    if problems:
        for p in problems:
            print("GAGAL:", p)
        return 1
    total = sum(len(lv["words"]) for lv in levels)
    print(f"OK: {len(levels)} level, {total} kata, semua aturan terpenuhi.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
