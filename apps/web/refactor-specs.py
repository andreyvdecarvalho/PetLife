import os
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = content.replace(': any', ': Record<string, unknown>')
    new_content = new_content.replace('let originalFileReader: Record<string, unknown>;', 'let originalFileReader: unknown;')
    new_content = new_content.replace('let originalImage: Record<string, unknown>;', 'let originalImage: unknown;')
    new_content = new_content.replace('let originalCreateElement: Record<string, unknown>;', 'let originalCreateElement: unknown;')
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print('Updated', filepath)

src_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'src')
for root, _, files in os.walk(src_dir):
    for file in files:
        if file.endswith('.spec.ts') or file.endswith('.spec.tsx'):
            process_file(os.path.join(root, file))
