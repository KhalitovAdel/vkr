#!/usr/bin/env python3
"""Extract slide titles and bullets from a .pptx file."""
import os
import re
import sys
import zipfile
import xml.etree.ElementTree as ET

A_NS = "{http://schemas.openxmlformats.org/drawingml/2006/main}"
P_NS = "{http://schemas.openxmlformats.org/presentationml/2006/main}"
R_NS = "{http://schemas.openxmlformats.org/officeDocument/2006/relationships}"


def para_text(p_elem):
    parts = []
    for t in p_elem.iter(f"{A_NS}t"):
        if t.text:
            parts.append(t.text)
        if t.tail:
            parts.append(t.tail)
    return re.sub(r"\s+", " ", "".join(parts)).strip()


def slide_paragraphs(slide_root):
    out = []
    seen = set()
    for tx in slide_root.iter(f"{P_NS}txBody"):
        for ap in tx.findall(f".//{A_NS}p"):
            txt = para_text(ap)
            if txt and txt not in seen:
                seen.add(txt)
                out.append(txt)
    return out


def extract(path: str) -> list[dict]:
    slides = []
    with zipfile.ZipFile(path) as z:
        pres = ET.fromstring(z.read("ppt/presentation.xml"))
        rels = ET.fromstring(z.read("ppt/_rels/presentation.xml.rels"))
        rid_to_target = {}
        for rel in rels:
            if rel.get("Type", "").endswith("/slide"):
                rid_to_target[rel.get("Id")] = rel.get("Target")

        slide_paths = []
        for sld_id in pres.findall(f".//{P_NS}sldId"):
            rid = sld_id.get(R_NS + "id")
            target = rid_to_target.get(rid, "")
            if target.startswith("/"):
                target = target.lstrip("/")
            if not target.startswith("ppt/"):
                target = "ppt/" + target.replace("../", "")
            slide_paths.append(target)

        for num, spath in enumerate(slide_paths, 1):
            slide_root = ET.fromstring(z.read(spath))
            paras = slide_paragraphs(slide_root)
            title = paras[0] if paras else ""
            bullets = paras[1:] if len(paras) > 1 else []
            slides.append({"num": num, "title": title, "bullets": bullets})
    return slides


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
        os.path.dirname(__file__), "..", "rubannv перезентация.pptx"
    )
    path = os.path.abspath(path)
    if not os.path.isfile(path):
        print(f"ERROR: not found: {path}", file=sys.stderr)
        return 1
    print(f"File: {path} ({os.path.getsize(path)} bytes)\n")
    for s in extract(path):
        print(f"--- SLIDE {s['num']} ---")
        print(s["title"] or "(no text)")
        for b in s["bullets"]:
            print(f"  • {b}")
        print()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
