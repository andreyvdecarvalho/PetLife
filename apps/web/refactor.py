import os
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = re.sub(r'catch\s*\(\s*err\s*:\s*any\s*\)', 'catch (err: unknown)', content)
    new_content = re.sub(r'catch\s*\(\s*e\s*:\s*any\s*\)', 'catch (e: unknown)', new_content)
    new_content = re.sub(r'catch\s*\(\s*error\s*:\s*any\s*\)', 'catch (error: unknown)', new_content)
    
    # Replacing common error handling pattern (err as any).message etc.
    new_content = new_content.replace('err.response?.data?.message', '(err as any).response?.data?.message')
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print('Updated', filepath)

src_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'src')
for root, _, files in os.walk(src_dir):
    for file in files:
        if file.endswith('.ts') or file.endswith('.tsx'):
            process_file(os.path.join(root, file))
